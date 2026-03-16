import zipfile, os

jar_path = None
for root, dirs, files in os.walk('C:/dev/StaffRadar/.gradle'):
    for f in files:
        if 'improperui' in f and f.endswith('.jar'):
            jar_path = os.path.join(root, f)
            break
    if jar_path: break

if jar_path:
    with zipfile.ZipFile(jar_path, 'r') as z:
        for info in z.infolist():
            if info.filename.endswith('UICallback.class') or info.filename.endswith('ImproperUIAPI.class'):
                print(info.filename)
