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
from ollama import chat

import argparse 
import json

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


# Process files from queue
while not file_queue.empty():
    file_path = file_queue.get()

    response = chat(
        model=args.aipath,
        messages=[{
            'role': 'user',
            'content': 'Describe this image, Look for 6 Categories and output their results in the format of (WhatItIs: Value), at the end please provide a confidence rating in the answers. Here is Categories: SampleID which looks like a barcode with a string of numbers underneath it, Date Sampled which has the words Date and then potentially a date, not always one, Machine which either has RN: UP and a string of numbers OR it is Equip Id(Please specifiy which one it is), Serial Number which says SN: and has a string of numbers and letters, and Reference Lube which says a string of text',
            'images': [file_path]
        }],
    )

    result = json.dumps({
        "file": file_path,
        "response": response.message.content
    })
    print(result)

    with open("aioutput.log", "a") as outputfile:
        outputfile.write(result + "\n")