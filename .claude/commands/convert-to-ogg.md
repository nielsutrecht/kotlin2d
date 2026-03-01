---
name: "Convert to OGG"
description: Convert MP3 or WAV files to OGG format using ffmpeg
category: Audio
tags: [audio, conversion, ffmpeg]
---

Convert audio files (MP3, WAV) to OGG Vorbis format using ffmpeg.

**Input**: File path(s) or glob pattern after the command. If omitted, ask the user which files to convert.

**Steps**

1. First verify ffmpeg is installed by running `ffmpeg -version`. If not found, tell the user to install it.

2. Resolve the input files:
   - If a specific file path is given, use it
   - If a glob pattern is given (e.g., `*.mp3`), expand it
   - If a directory is given, find all `.mp3` and `.wav` files in it
   - If no input, ask the user which files to convert

3. For each input file, convert to OGG using:
   ```bash
   ffmpeg -i "<input>" -c:a libvorbis -q:a 4 "<output>.ogg"
   ```
   - Output file has the same name with `.ogg` extension, placed in the same directory
   - `-q:a 4` is a reasonable quality level (scale 0-10, 4 ≈ 128kbps)
   - Skip files that are already `.ogg`
   - If the output file already exists, ask before overwriting

4. Show a summary of converted files.

**Notes**
- Do NOT delete the original files unless the user explicitly asks
- If converting many files, show progress
