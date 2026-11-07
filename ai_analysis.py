"""
config:
    use a built-in prompt

return:
    text as json
"""

###################################################

# add arguments
import argparse

parser = argparse.ArgumentParser("")
parser.add_argument("aipath", help="Path to the AI file")
parser.add_argument("--image", default="?", help="Path to image file to input to the AI")
parser.add_argument("--video", default="?", help="Path to video file to input to the AI")
args = parser.parse_args()


print("aipath: ", args.aipath)
print("image: ", args.image)
print("video: ", args.video)

###################################################

# ai goes here
"""
from ollama import chat


response = chat(
    model='batiai/qwen3-vl-embed-2b',
    messages=[{'role': 'user', 'content': 'what is the capital of france'}],
)
print(response.message.content)
"""

import torch
from Qwen3_VL_Embedding_main.src.models.qwen3_vl_embedding import Qwen3VLEmbedder

model = Qwen3VLEmbedder(
    model_name_or_path="./models/Qwen3-VL-Embedding-2B",
    # flash_attention_2 for better acceleration and memory saving
    # torch_dtype=torch.bfloat16, 
    # attn_implementation="flash_attention_2"
)

inputs = [{
    "text": "A woman playing with her dog on a beach at sunset.",
    "instruction": "Retrieve images or text relevant to the user's query.",
}, {
    "text": "A woman shares a joyful moment with her golden retriever on a sun-drenched beach at sunset, as the dog offers its paw in a heartwarming display of companionship and trust."
}, {
    "image": "https://qianwen-res.oss-cn-beijing.aliyuncs.com/Qwen-VL/assets/demo.jpeg"
}, {
    "text": "A woman shares a joyful moment with her golden retriever on a sun-drenched beach at sunset, as the dog offers its paw in a heartwarming display of companionship and trust.", 
    "image": "https://qianwen-res.oss-cn-beijing.aliyuncs.com/Qwen-VL/assets/demo.jpeg"
}]

embeddings = model.process(inputs)
print(embeddings @ embeddings.T)


###################################################

# input image to AI if input is selected
if (args.image == "?"):
    donothing = ""
else:
    print("image arg exists")


###################################################

# input video to AI if input is selected
if (args.video == "?"):
    donothing = ""
else:
    print("video arg exists")


###################################################



"""
ai output must be in 1 line
"""



"""
output json
"""

# output to file aioutput.log
with open("aioutput.log", "a") as outputfile:
  outputfile.write("aipath: " + args.aipath)
  outputfile.write("image: " + args.image)
  outputfile.write("video: " + args.video)