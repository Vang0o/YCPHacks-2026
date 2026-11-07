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

from ollama import chat


response = chat(
    model='batiai/qwen3-vl-embed-2b',
    messages=[{'role': 'user', 'content': 'what is the capital of france'}],
)
print(response.message.content)



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