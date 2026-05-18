import requests
import json
import os

# 1. Configuration
# Change to https and your specific IP
BASE_URL = "https://172.16.204.139:8080/gemfire-api/v1"
REGION_NAME = "Customer"
headers = {'Content-Type': 'application/json'}

# 2. Path to your certificate file (.crt)
# Ensure this file is in the same folder as this script, or provide the full path
CERT_FILE = "server.crt" 

def gemfire_demo():
    # Check if cert exists to avoid confusing error messages
    if not os.path.exists(CERT_FILE):
        print(f"Error: Certificate file '{CERT_FILE}' not found in current directory.")
        return

    # --- 1. Create a new entry (PUT) ---
    customer_data = {
        "id": 101,
        "name": "Jane Doe",
        "email": "jane.doe@example.com",
        "status": "Active"
    }
    
    put_url = f"{BASE_URL}/{REGION_NAME}/101"
    
    try:
        # Use verify=CERT_FILE to handle the TLS handshake
        response = requests.put(
            put_url, 
            data=json.dumps(customer_data), 
            headers=headers, 
            verify=CERT_FILE
        )
        
        if response.status_code in [200, 201]:
            print("Successfully created/updated customer.")
        else:
            print(f"Failed to create customer. Status: {response.status_code}, Text: {response.text}")

        # --- 2. Get the entry back (GET) ---
        get_url = f"{BASE_URL}/{REGION_NAME}/101"
        response = requests.get(get_url, verify=CERT_FILE)
        
        if response.status_code == 200:
            print("Retrieved Data:", response.json())
        else:
            print(f"Failed to retrieve data. Status: {response.status_code}")

    except requests.exceptions.SSLError as ssl_err:
        print(f"SSL/TLS Error: Ensure GemFire is started with the correct JKS. Detail: {ssl_err}")
    except requests.exceptions.ConnectionError as conn_err:
        print(f"Connection Error: Is GemFire running on https://172.16.204.139:8080? {conn_err}")

if __name__ == "__main__":
    gemfire_demo()
