import sys
import json
import requests

MCP_URL = "http://127.0.0.1:8085/mcp"
MCP_AUTH = ("admin", "password")
LLAMA_SERVER_URL = "http://127.0.0.1:8080/v1/chat/completions"

class GemFireAgent:
    def __init__(self):
        self.session = requests.Session()
        self.session_id = None
        self.tools = []
        self.openai_tools = []

    def _parse_mcp_response(self, res):
        """兼容解析标准 JSON 与 SSE 格式 (data: {...})"""
        if res.status_code not in (200, 201, 202):
            raise RuntimeError(f"服务端返回 HTTP {res.status_code}: {res.text}")
        
        text = res.text.strip()
        if not text:
            return {}

        # 1. 尝试直接按标准 JSON 解析
        try:
            return json.loads(text)
        except json.JSONDecodeError:
            pass

        # 2. 兼容 SSE 流式数据块解析
        for line in text.splitlines():
            line = line.strip()
            if line.startswith("data:"):
                payload = line[5:].strip()
                try:
                    return json.loads(payload)
                except json.JSONDecodeError:
                    continue

        raise RuntimeError(f"无法解析服务端响应 (HTTP {res.status_code}):\n{text}")

    def connect_mcp(self):
        print("[1/4] 发起 MCP Streamable HTTP 握手 (initialize)...")
        init_payload = {
            "jsonrpc": "2.0",
            "id": "1",
            "method": "initialize",
            "params": {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {"name": "gemfire-python-agent", "version": "1.0.0"}
            }
        }
        res = self.session.post(
            MCP_URL,
            auth=MCP_AUTH,
            json=init_payload,
            headers={
                "Content-Type": "application/json",
                "Accept": "application/json, text/event-stream"
            }
        )
        init_data = self._parse_mcp_response(res)
        
        # 提取服务端下发的 Session ID（忽略大小写）
        for k, v in res.headers.items():
            if k.lower() == "mcp-session-id":
                self.session_id = v
                break
                
        if not self.session_id:
            raise RuntimeError(f"未找到 Mcp-Session-Id！响应头: {dict(res.headers)}")
        print(f" -> 握手成功，获取 Session ID: {self.session_id}")

        # MCP 协议关键步骤：发送 initialized 通知确认
        print("[2/4] 发送握手确认通知 (notifications/initialized)...")
        notify_payload = {
            "jsonrpc": "2.0",
            "method": "notifications/initialized",
            "params": {}
        }
        self.session.post(
            MCP_URL,
            auth=MCP_AUTH,
            json=notify_payload,
            headers={
                "Mcp-Session-Id": self.session_id,
                "Content-Type": "application/json",
                "Accept": "application/json, text/event-stream"
            }
        )

        # 获取可用工具列表
        print("[3/4] 拉取 GemFire 工具清单 (tools/list)...")
        list_payload = {
            "jsonrpc": "2.0",
            "id": "2",
            "method": "tools/list",
            "params": {}
        }
        res = self.session.post(
            MCP_URL,
            auth=MCP_AUTH,
            json=list_payload,
            headers={
                "Mcp-Session-Id": self.session_id,
                "Content-Type": "application/json",
                "Accept": "application/json, text/event-stream"
            }
        )
        data = self._parse_mcp_response(res)
        self.tools = data.get("result", {}).get("tools", [])
        print(f" -> 成功获取 {len(self.tools)} 个工具！")

        # 转换为大模型 Function Calling 格式
        self.openai_tools = [
            {
                "type": "function",
                "function": {
                    "name": t["name"],
                    "description": t.get("description", ""),
                    "parameters": t.get("inputSchema", {"type": "object", "properties": {}})
                }
            }
            for t in self.tools
        ]

    def execute_tool(self, tool_name, tool_args):
        print(f"\n[MCP 执行] 工具名: {tool_name}")
        print(f"  参数: {json.dumps(tool_args, ensure_ascii=False)}")
        call_payload = {
            "jsonrpc": "2.0",
            "id": "3",
            "method": "tools/call",
            "params": {
                "name": tool_name,
                "arguments": tool_args
            }
        }
        res = self.session.post(
            MCP_URL,
            auth=MCP_AUTH,
            json=call_payload,
            headers={
                "Mcp-Session-Id": self.session_id,
                "Content-Type": "application/json",
                "Accept": "application/json, text/event-stream"
            }
        )
        result_json = self._parse_mcp_response(res)
        content = result_json.get("result", {}).get("content", [])
        output_text = "\n".join([item.get("text", "") for item in content if item.get("type") == "text"])
        if not output_text:
            output_text = json.dumps(result_json.get("result", {}), ensure_ascii=False)
        return output_text

    def ask(self, user_prompt):
        messages = [
            {
                "role": "system",
                "content": (
                    "你是一个资深 GemFire 集群运维专家。根据用户问题调用对应的 GemFire 工具，"
                    "并依据返回的监控指标生成专业的健康诊断与分析报告。"
                )
            },
            {"role": "user", "content": user_prompt}
        ]

        print(f"\n[用户提问]: {user_prompt}")
        print("[LLM 推理中...]")

        llama_payload = {
            "model": "Qwen2.5-7B-Instruct",
            "messages": messages,
            "tools": self.openai_tools,
            "tool_choice": "auto",
            "temperature": 0.2
        }

        resp = requests.post(LLAMA_SERVER_URL, json=llama_payload).json()
        choice = resp["choices"][0]
        message = choice["message"]

        if message.get("tool_calls"):
            tool_calls = message["tool_calls"]
            messages.append(message)

            for tc in tool_calls:
                func_name = tc["function"]["name"]
                raw_args = tc["function"].get("arguments", "{}")
                func_args = json.loads(raw_args) if isinstance(raw_args, str) else raw_args

                tool_output = self.execute_tool(func_name, func_args)

                messages.append({
                    "role": "tool",
                    "tool_call_id": tc["id"],
                    "content": tool_output
                })

            print("[LLM 综合诊断分析中...]")
            final_resp = requests.post(
                LLAMA_SERVER_URL,
                json={
                    "model": "Qwen2.5-7B-Instruct",
                    "messages": messages,
                    "temperature": 0.2
                }
            ).json()
            return final_resp["choices"][0]["message"]["content"]
        else:
            return message.get("content", "")

if __name__ == "__main__":
    agent = GemFireAgent()
    try:
        agent.connect_mcp()
    except Exception as e:
        print(f"初始化异常: {e}")
        sys.exit(1)

    print("\n" + "="*50)
    print("GemFire MCP 诊断终端已就绪 (输入 'exit' 或 'quit' 退出)")
    print("="*50)

    # 默认触发一次集群健康评估
    default_prompt = "请评估当前 GemFire 集群的整体健康状态，并指出存在的问题。"
    print(f"\n执行默认诊断: {default_prompt}")
    report = agent.ask(default_prompt)
    print("\n--- [GemFire 诊断分析报告] ---")
    print(report)
    print("--------------------------------\n")

    while True:
        try:
            user_input = input("请输入诊断需求 > ").strip()
            if not user_input or user_input.lower() in ("exit", "quit"):
                break
            result = agent.ask(user_input)
            print("\n--- [诊断结果] ---")
            print(result)
            print("--------------------\n")
        except KeyboardInterrupt:
            break
