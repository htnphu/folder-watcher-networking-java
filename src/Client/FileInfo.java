package Client;

import java.io.File;

public class FileInfo {
  File file;
  long lastModified;
  FileInfo(String path){
    this.file = new File(path);
    this.lastModified = file.lastModified();
  }
  public boolean hasChanged(){
    if(this.file.lastModified() != this.lastModified){
      this.lastModified = this.file.lastModified();
      return true;
    }
    return false;
  }
  public String changeMsg(){
    return String.format("%s&&%d", this.file.getName(),this.lastModified);
  }
  @Override
  public String toString() {
    return this.file.getName();
  }
}
