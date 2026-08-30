import os

def replace_in_file(path, replacements):
    if not os.path.exists(path):
        return
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
        
replacements_gradle = [
    ('applicationId = "studio.cluvex.aether"', 'applicationId = "app.psither"'),
]
replace_in_file('app/build.gradle.kts', replacements_gradle)

replacements_strings = [
    ('Aether', 'Psither'),
]
replace_in_file('app/src/main/res/values/strings.xml', replacements_strings)
replace_in_file('app/src/main/res/values-fa/strings.xml', replacements_strings)

replacements_readme = [
    ('Aether', 'Psither'),
    ('aether-psiphon', 'psither'),
    ('aether', 'psither'),
    ('AETHER', 'PSITHER'),
]
replace_in_file('README.md', replacements_readme)
replace_in_file('README.fa.md', replacements_readme)

replacements_workflow = [
    ('Aether', 'Psither'),
    ('aether', 'psither'),
]
replace_in_file('.github/workflows/build.yml', replacements_workflow)

print('Done')
