package snow.prog.fhbgds;

public class MainClass {

	public static void main(String[] args){
		try {
			Runtime.getRuntime().exec("javaw.exe -Djava.library.path=\"./Snow_Avoider_lib\" -cp Snow_Avoider.jar snow.prog.fhbgds.Snow");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
