from flask import Flask, request, jsonify
from PIL import Image
import torch
from transformers import Blip2Processor, Blip2ForConditionalGeneration

app = Flask(__name__)

# Load model and processor
device = "cuda" if torch.cuda.is_available() else "cpu"
processor = Blip2Processor.from_pretrained("Salesforce/blip2-opt-2.7b")
model = Blip2ForConditionalGeneration.from_pretrained("Salesforce/blip2-opt-2.7b", torch_dtype=torch.float16 if device == "cuda" else torch.float32)
model.to(device)

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files or 'question' not in request.form:
        return jsonify({'error': 'Image and question are required'}), 400

    image = Image.open(request.files['image']).convert('RGB')
    question = request.form['question']

    inputs = processor(images=image, text=question, return_tensors="pt").to(device)
    output = model.generate(**inputs)
    answer = processor.decode(output[0], skip_special_tokens=True)

    return jsonify({'answer': answer})

if __name__ == '__main__':
    app.run(port=5000)
