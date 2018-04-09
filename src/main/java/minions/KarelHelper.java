package minions;


public class KarelHelper {
	
	private static final String[] REJECT_NAMES = {
		"random",
		"paintCorner",
		"cornerColorIs",
		"println"
	};

	public static void validateName(String name) {
		for(String reject : REJECT_NAMES) {
			if(name.equals(reject)) {
				throw new RuntimeException("parse error");
			}
		}
		
	}

}
