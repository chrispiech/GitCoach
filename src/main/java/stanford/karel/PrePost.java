package stanford.karel;


import org.json.JSONObject;


public class PrePost {

	private KarelState precondition;
	private KarelState postcondition;
	
	public PrePost(KarelState before, KarelState after) {
		this.precondition = before;
		this.postcondition = after;
	}
	
	public PrePost(String jsonString) {
		JSONObject json = new JSONObject(jsonString);
		this.precondition = new KarelState(json.getJSONObject("pre"));
		this.postcondition = new KarelState(json.getJSONObject("post"));
	}

	public JSONObject getJson() {
		JSONObject root = new JSONObject();
		root.put("pre", precondition.getJson());
		root.put("post", postcondition.getJson());
		return root;
	}

	public KarelState getPrecondition() {
		return precondition;
	}

	public KarelState getPostcondition() {
		return postcondition;
	}
	
}
