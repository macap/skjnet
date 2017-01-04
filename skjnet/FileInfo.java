package skjnet;

class FileInfo {
	public int appId = -1;
	public String name, hash;
	public int size;
	public FileInfo(String n, String h, int s) {
		name =n; hash=h; size=s;
	}
	public String toString() {
		return "S"+appId+'\t'+name+'\t'+hash+'\t'+size;
	}
}