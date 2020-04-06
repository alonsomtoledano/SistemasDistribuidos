import cv2
import pytesseract
import sys
import os

pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

img = cv2.imread(sys.argv[1])
text = pytesseract.image_to_string(img)

matricula = text [1:len(text)]

f=open("./src/cameraRing/matriculas.txt","a")
f.write(matricula + "\n")
os.remove(sys.argv[1])

f.close()