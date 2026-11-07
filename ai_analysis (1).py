"""
config:
    use a built-in prompt

return:
    text as json
"""


# ai goes here
"""
from ollama import chat


response = chat(
    model='batiai/qwen3-vl-embed-2b',
    messages=[{'role': 'user', 'content': 'what is the capital of france'}],
)
print(response.message.content)
"""





"""
ai output must be in 1 line
"""



"""
output json
"""

###################################################

# arguments
import argparse 
from Qwen3_VL_Embedding_main.src.models.qwen3_vl_embedding import Qwen3VLEmbedder
#path when downloading, replaced when not added at next step
parser = argparse.ArgumentParser("")
parser.add_argument("aipath", help="Path to the AI file")
parser.add_argument("--image", default="?", help="Path to image file to input to the AI")
parser.add_argument("--video", default="?", help="Path to video file to input to the AI")
args = parser.parse_args()


print("aipath: ", args.aipath)
print("image: ", args.image)
print("video: ", args.video)


###################################################

#This is the queue system for multiple files given from stage one
import queue
import os

#files from stage 1
files = []
if args.image != "?":
        files.append(args.image)
if args.video != "?":
        files.append(args.video)


def enqueue_files(file_paths):
    longLine = queue.Queue()
    for path in file_paths:
        if os.path.isfile(path):
            longLine.put(path)
        else:
            print(f"Skipping: {path} (not a file)")
    return longLine

  
file_queue = enqueue_files(files)

model = Qwen3VLEmbedder(model_name_or_path=args.aipath)

# Process files from queue
while not file_queue.empty():
    file_path = file_queue.get()
    #change to what we need for it
    inputs = [{"image": file_path}]
    embeddings = model.process(inputs)

###################################################


# output to file aioutput.log
with open("aioutput.log", "a") as outputfile:
  outputfile.write("aipath: " + args.aipath)
  outputfile.write("image: " + args.image)
  outputfile.write("video: " + args.video)

