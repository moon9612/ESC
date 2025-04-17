from flask import Flask, request, jsonify
from flask_cors import CORS
from openai import OpenAI
from dotenv import load_dotenv
import os, time

load_dotenv()

app = Flask(__name__)
CORS(app)

openai_api_key = os.getenv("openai_api_key")
assistant_id = os.getenv("assistant_id")
client = OpenAI(api_key=openai_api_key)

thread_id = None

@app.route('/chat', methods=['POST'])
def chat():
    global thread_id
    user_input = request.json.get("message")

    if not thread_id:
        thread = client.beta.threads.create()
        thread_id = thread.id

    client.beta.threads.messages.create(
        thread_id=thread_id,
        role="user",
        content=user_input
    )

    run = client.beta.threads.runs.create(
        thread_id=thread_id,
        assistant_id=assistant_id
    )

    while True:
        run_status = client.beta.threads.runs.retrieve(
            thread_id=thread_id,
            run_id=run.id
        )
        if run_status.status == "completed":
            break
        time.sleep(0.2)

    messages = client.beta.threads.messages.list(thread_id=thread_id)
    latest = messages.data[0].content[0].text.value
    return jsonify({"reply": latest})

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)
