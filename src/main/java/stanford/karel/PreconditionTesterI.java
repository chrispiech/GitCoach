package stanford.karel;

import java.util.List;

public interface PreconditionTesterI {

	public List<PrePost> test(List<KarelState> toTest);
	
}
