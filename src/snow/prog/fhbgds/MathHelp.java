package snow.prog.fhbgds;

public class MathHelp {

	public static boolean isBetween(float numInQuestion, float num1, float num2){
		if(num1 <= numInQuestion && num2 >= numInQuestion){
			return true;
		}
		return false;
	}
}
