import cv2
import pytesseract
import sys
import os

pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

img = cv2.imread(sys.argv[1])
text = pytesseract.image_to_string(img)

matricula = text [1:len(text)]

os.remove(sys.argv[1])

if sys.argv[2] == "true":
	f = open("./src/cameraRing/matriculas.txt","a")
	f.write(matricula + "\n")

	f.close()
else:
	f = open("./src/cameraRing/matriculas.txt","r")
	lines = f.readlines()
	
	f.close()
	
	f = open("./src/cameraRing/matriculas.txt","w")
	
	for line in lines:
		if line != matricula + "\n":
			f.write(line)
			
	f.close()