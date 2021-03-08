package bitcoinlistener;

import java.util.List;

public interface BitcoinConnection {
	boolean isConnected();
	void disconnect() throws Exception;
	String getIp();
	int getPort();
	NetworkParameters getNetworkParameters();
	void sendMessage(ProtocolMessage msg);
	
	void addFilter(List<String> address);
	void addFilter(String address);
	void setFilterConfig(FilterConfig filterConfig);
	FilterConfig getFilterConfig();
}
