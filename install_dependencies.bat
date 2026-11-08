powershell -command "winget install Python.Python.3.14"
powershell -command "irm https://ollama.com/install.ps1 | iex"
powershell -command "python -m pip install ollama"
powershell -command "ollama pull lfm3-omni"