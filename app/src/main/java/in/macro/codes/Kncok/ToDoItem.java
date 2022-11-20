package in.macro.codes.Kncok;

public class ToDoItem {

    public ToDoItem(String text,
                    String id,
                    String containerName,
                    String resourceName,
                    String imageUri,
                    String sasQueryString) {

        this.setContainerName(containerName);
        this.setResourceName(resourceName);
        this.setImageUri(imageUri);
        this.setSasQueryString(sasQueryString);
    }

    public ToDoItem() {
        mContainerName = "";
        mResourceName = "";
        mImageUri = "";
        mSasQueryString = "";

}
    /**
     *  imageUri - points to location in storage where photo will go
     */
    @com.google.gson.annotations.SerializedName("imageUri")
    private String mImageUri;

    /**
     * Returns the item ImageUri
     */
    public String getImageUri() {
        return mImageUri;
    }

    /**
     * Sets the item ImageUri
     *
     * @param ImageUri
     *            Uri to set
     */
    public final void setImageUri(String ImageUri) {
        mImageUri = ImageUri;
    }

    /**
     * ContainerName - like a directory, holds blobs
     */
    @com.google.gson.annotations.SerializedName("containerName")
    private String mContainerName;

    /**
     * Returns the item ContainerName
     */
    public String getContainerName() {
        return mContainerName;
    }

    /**
     * Sets the item ContainerName
     *
     * @param ContainerName
     *            Uri to set
     */
    public final void setContainerName(String ContainerName) {
        mContainerName = ContainerName;
    }

    /**
     *  ResourceName
     */
    @com.google.gson.annotations.SerializedName("resourceName")
    private String mResourceName;

    /**
     * Returns the item ResourceName
     */
    public String getResourceName() {
        return mResourceName;
    }

    /**
     * Sets the item ResourceName
     *
     * @param ResourceName
     *            Uri to set
     */
    public final void setResourceName(String ResourceName) {
        mResourceName = ResourceName;
    }

    /**
     *  SasQueryString - permission to write to storage
     */
    @com.google.gson.annotations.SerializedName("sasQueryString")
    private String mSasQueryString;

    /**
     * Returns the item SasQueryString
     */
    public String getSasQueryString() {
        return mSasQueryString;
    }

    /**
     * Sets the item SasQueryString
     *
     * @param SasQueryString
     *            Uri to set
     */
    public final void setSasQueryString(String SasQueryString) {
        mSasQueryString = SasQueryString;
    }
}
