package front;

import java.math.BigInteger;
import java.util.Date;

public class CertificateInfo {

    private String alias;
    private Integer keySize;
    private String version = "v3";
    private Date dateFrom;
    private Date dateTo;
    private BigInteger serialNumber;
    private String commonName;
    private String organizationalUnit;
    private String organizationalName;
    private String localityName;
    private String stateName;
    private String countryName;
    private String emailAddress;
    private String publicKey;
    private String privateKey;

    public CertificateInfo(String alias, Integer keySize, Date dateFrom, Date dateTo,
            BigInteger serialNumber, String commonName,
            String organizationalUnit, String organizationalName,
            String localityName, String stateName, String countryName,
            String emailAddress, String publicKey, String privateKey) {
        this.alias = alias;
        this.keySize = keySize;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.serialNumber = serialNumber;
        this.commonName = commonName;
        this.organizationalUnit = organizationalUnit;
        this.organizationalName = organizationalName;
        this.localityName = localityName;
        this.stateName = stateName;
        this.countryName = countryName;
        this.emailAddress = emailAddress;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    
    
    
    
    
    
    
    
    
    
    //GET & SET
    public String getAlias() {
        return alias;
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public Integer getKeySize() {
        return keySize;
    }

    public void setKeySize(Integer keySize) {
        this.keySize = keySize;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getOrganizationalName() {
        return organizationalName;
    }

    public void setOrganizationalName(String organizationalName) {
        this.organizationalName = organizationalName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getVersion() {
        return version;
    }
}
