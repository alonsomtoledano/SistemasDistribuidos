import cv2
import pytesseract

pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

img = cv2.imread('matricula.jpg')
text = pytesseract.image_to_string(img)

matricula = text [1:len(text)]

f=open("matriculas.txt","w")
f.write(matricula + "\n")

f.close()

print(matricula)