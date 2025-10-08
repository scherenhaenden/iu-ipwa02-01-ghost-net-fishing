#!/usr/bin/env python3
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import Dict, Tuple, List

XML = Path('target/site/jacoco/jacoco.xml')
if not XML.exists():
    print(f'Coverage XML not found: {XML.resolve()}')
    raise SystemExit(1)

ns = {}
root = ET.parse(XML).getroot()

# helpers
def get_counter(elem, t='INSTRUCTION') -> Tuple[int,int]:
    for c in elem.findall('counter'):
        if c.attrib.get('type') == t:
            missed = int(c.attrib.get('missed', '0'))
            covered = int(c.attrib.get('covered', '0'))
            return missed, covered
    return 0,0

# Overall
overall_missed, overall_covered = get_counter(root, 'INSTRUCTION')
overall_total = overall_missed + overall_covered
instr_pct = (overall_covered / overall_total * 100) if overall_total else 100.0

line_missed, line_covered = get_counter(root, 'LINE')
line_total = line_missed + line_covered
line_pct = (line_covered / line_total *100) if line_total else 100.0

branch_missed, branch_covered = get_counter(root, 'BRANCH')
branch_total = branch_missed + branch_covered
branch_pct = (branch_covered / branch_total *100) if branch_total else 100.0

method_missed, method_covered = get_counter(root, 'METHOD')
method_total = method_missed + method_covered
method_pct = (method_covered / method_total *100) if method_total else 100.0

class_missed, class_covered = get_counter(root, 'CLASS')
class_total = class_missed + class_covered
class_pct = (class_covered / class_total *100) if class_total else 100.0

print('\nJaCoCo coverage summary (from target/site/jacoco/jacoco.xml)')
print('Overall:')
print(f'  Instructions: {overall_covered}/{overall_total} ({instr_pct:.1f}%)')
print(f'  Lines:        {line_covered}/{line_total} ({line_pct:.1f}%)')
print(f'  Branches:     {branch_covered}/{branch_total} ({branch_pct:.1f}%)')
print(f'  Methods:      {method_covered}/{method_total} ({method_pct:.1f}%)')
print(f'  Classes:      {class_covered}/{class_total} ({class_pct:.1f}%)')

# Per-package summary (use LINE coverage if present)
packages = []
for pkg in root.findall('package'):
    pkg_name = pkg.attrib.get('name')
    lm, lc = get_counter(pkg, 'LINE')
    it_m, it_c = get_counter(pkg, 'INSTRUCTION')
    lt = lm + lc
    itt = it_m + it_c
    line_pct_pkg = (lc/lt*100) if lt else None
    instr_pct_pkg = (it_c/itt*100) if itt else None
    packages.append((pkg_name, lt, lc, line_pct_pkg, itt, it_c, instr_pct_pkg))

# sort packages by line coverage percent descending (None -> treat as -1)
packages_sorted = sorted(packages, key=lambda x: (x[3] is not None, x[3] or -1), reverse=True)

print('\nPer-package (line coverage)')
print('  Package (lines covered/total) [instr%]')
for name, lt, lc, lpct, itt, itc, ipct in packages_sorted:
    lp = f'{lc}/{lt} {lpct:.1f}%' if lpct is not None else 'n/a'
    ip = f'{(itc/itt*100):.1f}%' if itt else 'n/a'
    print(f'  - {name.replace("/", "."):60} {lp:20} [instr {ip}]')

# Per-class: collect classes with instruction totals
classes = []
for pkg in root.findall('package'):
    for cls in pkg.findall('class'):
        cname = cls.attrib.get('name')
        im, ic = get_counter(cls, 'INSTRUCTION')
        lt = im + ic
        lm, lc = get_counter(cls, 'LINE')
        lt_lines = lm + lc
        if lt>0:
            pct = ic/lt*100
            classes.append((cname, lt, ic, pct, lt_lines, lc))

# sort by pct
classes_sorted = sorted(classes, key=lambda x: x[3], reverse=True)

print('\nTop 10 classes by instruction coverage (non-empty)')
for cname, total, covered, pct, lt_lines, lc in classes_sorted[:10]:
    print(f'  {pct:6.1f}%  {covered}/{total}  {cname}')

print('\nBottom 10 classes by instruction coverage (non-empty)')
for cname, total, covered, pct, lt_lines, lc in classes_sorted[-10:]:
    print(f'  {pct:6.1f}%  {covered}/{total}  {cname}')

# Classes with zero line coverage
zero_line = []
for pkg in root.findall('package'):
    for cls in pkg.findall('class'):
        name = cls.attrib.get('name')
        lm, lc = get_counter(cls, 'LINE')
        if (lm+lc)>0 and lc==0:
            zero_line.append((name, lm+lc))

if zero_line:
    print(f'\nClasses with lines but 0 lines covered: {len(zero_line)} (showing up to 20)')
    for name, total in zero_line[:20]:
        print(f'  0%   {name} (lines: {total})')

print('\nFull HTML report available at: target/site/jacoco/index.html')
print('XML: target/site/jacoco/jacoco.xml  CSV: target/site/jacoco/jacoco.csv')
print('\nDone.')

