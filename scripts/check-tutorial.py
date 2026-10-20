"""Offline repository consistency checks; this is NOT an Android build test."""
from pathlib import Path
from urllib.parse import unquote
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]


def main():
    errors = []
    markdown_files = list(ROOT.rglob("*.md"))
    for file in markdown_files:
        text = file.read_text(encoding="utf-8")
        if text.count("```") % 2:
            errors.append(f"Unclosed code fence: {file.relative_to(ROOT)}")
        # Ignore examples inside code fences; check actual Markdown links.
        prose = re.sub(r"```.*?```", "", text, flags=re.S)
        for target in re.findall(r"(?<!!)\[[^\]]+\]\(([^)]+)\)", prose):
            if re.match(r"[a-z]+://|mailto:|#", target):
                continue
            local = unquote(target.split("#")[0])
            if local and not (file.parent / local).exists():
                errors.append(f"Broken link: {file.relative_to(ROOT)} -> {target}")
    for xml in (ROOT / "examples/pocket-tasks/app/src/main").rglob("*.xml"):
        try:
            ET.parse(xml)
        except ET.ParseError as error:
            errors.append(f"Invalid XML: {xml}: {error}")
    required = ["README.md", "tutorial/sources.md", "examples/pocket-tasks/README.md", "templates/android.yml"]
    for name in required:
        if not (ROOT / name).is_file():
            errors.append(f"Missing tutorial file: {name}")
    # This is a documentation repository: no bundled dependencies, keys or APKs.
    forbidden = {".jar", ".apk", ".aab", ".zip", ".jks", ".keystore", ".p12"}
    for file in ROOT.rglob("*"):
        if file.is_file() and file.suffix.lower() in forbidden:
            errors.append(f"Unexpected binary or key in tutorial repository: {file.relative_to(ROOT)}")
    if errors:
        raise SystemExit("\n".join(errors))
    print(f"PASS: {len(markdown_files)} Markdown files, local links, code fences, Android XML, no bundled binaries/keys")
    print("Not checked: Kotlin compilation, Android lint, unit tests, installation, UI behavior.")


if __name__ == "__main__":
    main()
