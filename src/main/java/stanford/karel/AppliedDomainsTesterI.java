package stanford.karel;

import java.util.List;
import java.util.Map;

public interface AppliedDomainsTesterI {

	Map<String, List<PrePost>> getAppliedDomains(String unitTestDir);
	
}


