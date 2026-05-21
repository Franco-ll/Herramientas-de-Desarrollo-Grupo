import os

def check_brackets(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    stack = []
    brackets = {'(': ')', '{': '}', '[': ']'}
    
    for i, char in enumerate(content):
        if char in brackets.keys():
            stack.append((char, i))
        elif char in brackets.values():
            if not stack:
                return f"Extra closing bracket '{char}' at position {i}"
            top, pos = stack.pop()
            if brackets[top] != char:
                return f"Mismatched bracket '{char}' at position {i} (expected '{brackets[top]}' from position {pos})"
    
    if stack:
        top, pos = stack.pop()
        return f"Unclosed bracket '{top}' from position {pos}"
    
    return None

root_dir = r"c:\Users\sanan\Downloads\interfaces\avance 1 - herramientas de desarrollo\scholar-stay\src\main\java"

for root, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith(".java"):
            path = os.path.join(root, file)
            error = check_brackets(path)
            if error:
                print(f"ERROR in {path}: {error}")
            else:
                pass

print("Check complete.")
