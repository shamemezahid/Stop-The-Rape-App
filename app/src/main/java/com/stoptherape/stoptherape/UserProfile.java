package com.stoptherape.stoptherape;

class UserProfile {

    public String name;
    public String email;
    public String gender;
    public boolean shareLoc;

    public UserProfile() {
    }

    public UserProfile(String name, String email, String gender, boolean shareLoc) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.shareLoc = shareLoc;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public boolean getShareLoc(){
        return shareLoc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setShareLoc(boolean shareLoc){
        this.shareLoc = shareLoc;
    }
}
