import cv2
import pytesseract
import sys
import os

pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

img = cv2.imread(sys.argv[1])
text = pytesseract.image_to_string(img)

matricula = text [1:len(text)]

os.remove(sys.argv[1])
	
print(matricula)