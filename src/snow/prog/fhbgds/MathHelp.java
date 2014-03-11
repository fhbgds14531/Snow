package snow.prog.fhbgds;

public class MathHelp {

	public static boolean isBetween(int numInQuestion, int num1, int num2){
		if(num1 <= numInQuestion && num2 >= numInQuestion){
			return true;
		}
		return false;
	}
	
	public static boolean isBetween(float numInQuestion, int num1, int num2){
		return isBetween((int)numInQuestion, num1, num2);
	}

	public static boolean isBetween(int numInQuestion, float num1, float num2) {
		return isBetween(numInQuestion, (int) num1, (int) num2);
	}
	
}
