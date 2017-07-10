package persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import model.Board;

public class DBPersister{

	@SuppressWarnings("unchecked")
	public List<Board> loadDB(FileInputStream fis) throws IOException, ClassNotFoundException {
		ObjectInputStream ois= new ObjectInputStream(fis);
		return (List<Board>) ois.readObject();
	}

	public void saveDB(FileOutputStream fos, List<Board> data) throws IOException {
		ObjectOutputStream oos= new ObjectOutputStream(fos);
		oos.writeObject(data);
		oos.flush();
		oos.close();
	}

}
