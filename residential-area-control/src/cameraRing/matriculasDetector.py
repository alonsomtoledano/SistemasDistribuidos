import cv2
import pytesseract
import sys
import os
import shutil

pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

img = cv2.imread(sys.argv[1])
originPath = sys.argv[1]
imageName = sys.argv[2]
destinyPath = sys.argv[3] + imageName

text = pytesseract.image_to_string(img)

matricula = text [1:len(text)]

shutil.move(originPath, destinyPath)
	
print(matricula)