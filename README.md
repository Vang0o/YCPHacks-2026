MRG tube label analyzer.

The purpose of this program is to help 
This program allows users to input either one or multiple video or image file(s) and generates a .csv file


### Dependencies:
- Java 
- Python [https://apps.microsoft.com/detail/9pnrbtzxmb4z?hl=en-US&gl=US](https://apps.microsoft.com/detail/9pnrbtzxmb4z?hl=en-US&gl=US)
- Ollama (Download it with: `irm https://ollama.com/install.ps1 | iex`)


### Setup:
1. Install dependencies.
2. Enter this command in powershell: `irm https://ollama.com/install.ps1 | iex`
3. Enter this command in powershell: `python -m pip install ollama`
4. Enter this command in powershell: `ollama pull lfm3-omni`
5***. *optional, is to set Ollama as a path* $env:Path += ";%localappdata%\Programs\Ollama"
6. `python "ai_analysis (1).py" "lfm3-omni" --image "imagepath" --video "videopath"` 

Example: `python "ai_analysis (1).py" "lfm3-omni" --image "%appdata%\..\..\Downloads\Small_Label_Example_1.jpeg"`


### Usage:
1. Run `main.java`
