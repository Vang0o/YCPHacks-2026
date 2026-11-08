public class LabelConfig {

    private String name;
    private String type;
    private String sampleId;
    private String date;
    private String machine;
    private String component;
    private String serialNumber;
    private String referenceLube;

    public LabelConfig(String name, String type, String sampleId, String date,
                       String machine, String component,
                       String serialNumber, String referenceLube) {
        this.name = name;
        this.type = type;
        this.sampleId = sampleId;
        this.date = date;
        this.machine = machine;
        this.component = component;
        this.serialNumber = serialNumber;
        this.referenceLube = referenceLube;
    }

    public String getName(){ return name; }
    public String getType(){ return type; }
    public String getSampleId(){ return sampleId; }
    public String getDate(){ return date; }
    public String getMachine(){ return machine; }
    public String getComponent(){ return component; }
    public String getSerialNumber(){ return serialNumber; }
    public String getReferenceLube(){ return referenceLube; }

    public void setName(String v){ this.name = v; }
    public void setType(String v){ this.type = v; }
    public void setSampleId(String v){ this.sampleId = v; }
    public void setDate(String v){ this.date = v; }
    public void setMachine(String v){ this.machine = v; }
    public void setComponent(String v){ this.component = v; }
    public void setSerialNumber(String v){ this.serialNumber = v; }
    public void setReferenceLube(String v){ this.referenceLube = v; }
}