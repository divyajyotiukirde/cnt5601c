package processes;

public interface FileObserver {

    public void updateFileDownloadFinished();
    public void updateFilePartArrived (int part_index);
}
