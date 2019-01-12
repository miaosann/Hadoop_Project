
	
	
import sys
import synonyms
def nearbyWords(keyword):
    words = synonyms.nearby(keyword)
    if words != None:
        return words[0]
    else:
        return None


if __name__ == '__main__':
    a = []
    for i in range(1, len(sys.argv)):
        a.append(sys.argv[i])
    print(nearbyWords(a[0]))

