import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

/**
 * WATCH SERVICE API
 * REFERENCE:
 * https://gpcoder.com/4156-gioi-thieu-watchservice-api-trong-java/
 * https://docs.oracle.com/javase/7/docs/api/java/nio/file/WatchService.html
 */

public class Client {
	private final WatchService watchService;
	private final Map<WatchKey, Path> watchKey;

	public static void main(String[] args) throws IOException {
		new Client();
	}

	public Client() throws IOException {
		Path dir;
		this.watchKey = new HashMap<>();
		this.watchService = FileSystems.getDefault().newWatchService();
		try {
			while (true) {
				{
					// Reference: Mr. Nguyen Van Khiet - Java Networking
					Socket s = new Socket("localhost", 3200);
					DataInputStream disServer = new DataInputStream(s.getInputStream());
					DataInputStream disID = new DataInputStream(s.getInputStream());
					String clientID = disID.readUTF();
					String directory = disServer.readUTF();
					System.out.println(clientID);
					System.out.println(directory);
					dir = Paths.get(directory);
					walkFileTree(dir);

					// Handling event, takes s <-> socket as param
					eventHandler(s);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void eventHandler(Socket s) {
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			while (true) {
				WatchKey wk;
				try {
					wk = watchService.take();
				} catch (InterruptedException iex) {
					return;
				}

				Path dir = watchKey.get(wk);
				if (dir == null) {
					System.err.println("Failed due to watcherKey");
					continue;
				}

				for (WatchEvent<?> event : wk.pollEvents()) {
					@SuppressWarnings("rawtypes")
					WatchEvent.Kind kind = event.kind();
					@SuppressWarnings("unchecked")
					Path name = ((WatchEvent<Path>) event).context();
					Path childItems = dir.resolve(name);
					System.out.format("%s: %s\n", event.kind().name(), childItems);
					dos.writeUTF(event.kind().name() + "~" + childItems);
					dos.flush();
					if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
						try {
							if (Files.isDirectory(childItems)) {
								walkFileTree(childItems);
							}
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}
					}
				}
				boolean isValid = wk.reset();
				if (!isValid) {
					watchKey.remove(wk);
					if (watchKey.isEmpty()) {
						break;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void regDir(Path dir) throws IOException {
		/**
		 * ENTRY_MODIFY: CHANGE ON DIRECTORY
		 * ENTRY_DELETE: DELETE FILES/FOLDERS ON DIRECTORY
		 * ENTRY_CREATE: CREATE FILES/FOLDERS ON DIRECTORY
		 */
		WatchKey key = dir.register(watchService,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_CREATE);
		watchKey.put(key, dir);
	}

	private void walkFileTree(final Path start) throws IOException {
		Files.walkFileTree(start, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				// registry directory
				regDir(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
