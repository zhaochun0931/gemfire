import requests
import json

# Configuration
#BASE_URL = "http://localhost:8080/gemfire-api/v1"
BASE_URL = "http://172.16.204.139:8080/gemfire-api/v1"
REGION_NAME = "Customer"
headers = {'Content-Type': 'application/json'}

def gemfire_demo():
    # 1. Create a new entry (PUT)
    customer_data = {
        "id": 101,
        "name": "Jane Doe",
        "email": "jane.doe@example.com",
        "status": "Active"
    }
    
    put_url = f"{BASE_URL}/{REGION_NAME}/101"
    response = requests.put(put_url, data=json.dumps(customer_data), headers=headers)
    
    if response.status_code in [200, 201]:
        print("Successfully created/updated customer.")
    else:
        print(f"Failed to create customer: {response.text}")

    # 2. Get the entry back (GET)
    get_url = f"{BASE_URL}/{REGION_NAME}/101"
    response = requests.get(get_url)
    
    if response.status_code == 200:
        print("Retrieved Data:", response.json())

if __name__ == "__main__":
    gemfire_demo()
