package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MapperTable(nombre = "piiUser")
public class User extends PiikObject{
    @MapperColumn(notNull = true)
    private String email;
    @MapperColumn(notNull = true)
    private String name;
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn(notNull = true)
    private String pass;
    @MapperColumn
    private String gender;
    @MapperColumn
    private String description;
    @MapperColumn
    private String home;
    @MapperColumn
    private String postalCode;
    @MapperColumn
    private String province;
    @MapperColumn
    private String country;
    @MapperColumn
    private String city;
    @MapperColumn
    private String birthplace;
    @MapperColumn(notNull = true, columna = "birthdate")
    private Timestamp birthday;
    @MapperColumn(hasDefault = true, notNull = true)
    private Timestamp registrationDate;
    @MapperColumn
    private Timestamp deathdate;
    @MapperColumn
    private String religion;
    @MapperColumn
    private String emotionalSituation;
    @MapperColumn
    private String job;
    @MapperColumn(columna = "profilePicture", fKeys = "multimediaImage:hash", targetClass = Multimedia.class)
    private Multimedia multimedia;
    private UserType type;
    private String oldID;
    private List<String> phones;


    public User(String name, String id, String email) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.type = UserType.user;
        phones=new ArrayList<>();
    }

    public void addPhone(String phone){
        phones.add(phone);
    }

    /**
     * Constructor for testing -> removeUser
     *
     * @param name
     * @param id
     * @param email
     * @param type  Test if user is of type Admin
     */
    public User(String id, String name, String email, String pass, UserType type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.type = type;
        phones=new ArrayList<>();
        //this.birthday = new Timestamp(System.currentTimeMillis());

    }

    public User(String id, String pass) {
        this.id = id;
        this.pass = pass;
        phones=new ArrayList<>();
    }

    /**
     * A empty constructor is needed to be created by reflexion
     */

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Timestamp getDeathdate() {
        return deathdate;
    }

    public void setDeathdate(Timestamp deathdate) {
        this.deathdate = deathdate;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getEmotionalSituation() {
        return emotionalSituation;
    }

    public void setEmotionalSituation(String emotionalSituation) {
        this.emotionalSituation = emotionalSituation;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getPK()
    {
        return id;
    }

    public String getOldID() {
        return oldID;
    }

    public void setOldID(String oldID) {
        this.oldID = oldID;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
   /*
    public boolean checkNotNull() {
        // Check that the primary keys are not null
        if (!checkPrimaryKey()) {
            return false;
        }

        // Check the attributes with restriction 'not null'
        if (getPass() == null || getPass().isEmpty()) {
            return false;
        }
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
        return getBirthday() != null;

    }*/

    /**
     * Function to know if an user is an administrator or a normal user
     *
     * @return Returns "true" if the user is an admin, otherwise return "false"
     */
    public boolean checkAdministrator() {
        return type.equals(UserType.administrator);
    }

    /**
     * Function to check that the primary keys are not null
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    /*public boolean checkPrimaryKey() {
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
        // Check that the primary keys are not null
        return getId() != null && !getId().isEmpty();
    }*/

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", pass='" + pass + '\'' +
                ", type='" + this.getType() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
