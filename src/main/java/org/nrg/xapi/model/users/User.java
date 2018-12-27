/*
 * web: org.nrg.xapi.model.users.User
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.entities.UserAuthI;
import org.nrg.xft.security.UserI;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * A transport container for user details. The {@link #isSecured() secured property} controls whether security-related
 * properties like password and salt are available. When a new user object is created from an existing user record in
 * XNAT, the secure flag is set to true. This prevents serializing beans with existing user accounts to prevent exposing
 * password data. Newly created beans have secure set to false by default to allow for serializing the bean for REST
 * calls with all data intact.
 */
@ApiModel(description = "Contains the properties that define a user on the system.")
public class User implements UserI {
    /**
     * The user's unique key.
     **/
    @ApiModelProperty(value = "The user's unique key.")
    public Integer getId() {
        return _id;
    }

    public void setId(final Integer id) {
        _id = id;
    }

    /**
     * The user's login name.
     **/
    @ApiModelProperty(value = "The user's login name.")
    public String getUsername() {
        return _username;
    }

    public void setUsername(final String username) {
        _username = username;
    }

    /**
     * The user's first name.
     **/
    @ApiModelProperty(value = "The user's first name.")
    public String getFirstName() {
        return _firstName;
    }

    @SuppressWarnings("unused")
    public void setFirstName(final String firstName) {
        _firstName = firstName;
    }

    /**
     * The user's last name.
     **/
    @ApiModelProperty(value = "The user's last name.")
    public String getLastName() {
        return _lastName;
    }

    @SuppressWarnings("unused")
    public void setLastName(final String lastName) {
        _lastName = lastName;
    }

    /**
     * The user's _email address.
     **/
    @ApiModelProperty(value = "The user's email address.")
    public String getEmail() {
        return _email;
    }

    public void setEmail(final String email) {
        _email = email;
    }

    /**
     * Whether the user is enabled.
     **/
    @ApiModelProperty(value = "Whether the user is enabled.")
    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(final boolean isEnabled) {
        _enabled = isEnabled;
    }

    /**
     * Whether the user is verified.
     **/
    @ApiModelProperty(value = "Whether the user is verified.")
    public Boolean isVerified() {
        return _verified;
    }

    public void setVerified(final boolean verified) {
        _verified = verified;
    }

    /**
     * The user's encrypted password.
     **/
    @ApiModelProperty(value = "The user's encrypted password.")
    public String getPassword() {
        return _secured ? null : _password;
    }

    public void setPassword(final String password) {
        _password = password;
    }

    /**
     * The _salt used to encrypt the user's _password.
     **/
    @ApiModelProperty(value = "The salt used to encrypt the user's password.")
    public String getSalt() {
        return _secured ? null : _salt;
    }

    @SuppressWarnings("unused")
    public void setSalt(final String salt) {
        _salt = salt;
    }

    /**
     * The date and time the user record was last modified.
     **/
    @ApiModelProperty(value = "The date and time the user record was last modified.")
    public Date getLastModified() {
        return _lastModified;
    }

    public void setLastModified(final Date lastModified) {
        _lastModified = lastModified;
    }

    /**
     * The user's authorization record used when logging in.
     **/
    @ApiModelProperty(value = "The user's authorization record used when logging in.")
    public UserAuthI getAuthorization() {
        return _secured ? null : _authorization;
    }

    /**
     * Sets the user's authorization record used when logging in.
     **/
    public UserAuthI setAuthorization(final UserAuthI authorization) {
        final UserAuthI existing = _authorization;
        _authorization = authorization;
        return existing;
    }

    /**
     * Returns the date and time of the last successful login attempt for the most recently used authentication provider.
     *
     * @return The date and time of the last successful login attempt for the most recently used authentication provider.
     */
    @ApiModelProperty("The date and time of the last successful login attempt for the most recently used authentication provider.")
    public Date getLastSuccessfulLogin() {
        return _lastSuccessfulLogin;
    }

    /**
     * Sets the date and time of the last successful login attempt for the most recently used authentication provider.
     */
    @SuppressWarnings("unused")
    public void setLastSuccessfulLogin(Date lastSuccessfulLogin) {
        _lastSuccessfulLogin = lastSuccessfulLogin;
    }

    @ApiModelProperty(value = "The user's full name.")
    @JsonIgnore
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    @ApiModelProperty(value = "Indicates whether the user object is secured, which causes secure fields like password and salt to return null.")
    public boolean isSecured() {
        return _secured;
    }

    public void setSecured(final boolean secured) {
        _secured = secured;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getID() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    public String getLogin() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isGuest() {
        return StringUtils.equalsIgnoreCase(GUEST_USERNAME, getUsername());
    }

    /**
     * {@inheritDoc}
     */
    public String getFirstname() {
        return getFirstName();
    }

    /**
     * {@inheritDoc}
     */
    public String getLastname() {
        return getLastName();
    }

    /**
     * {@inheritDoc}
     */
    public String getDBName() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive() {
        return isEnabled() && isVerified();
    }

    /**
     * {@inheritDoc}
     */
    public void setLogin(final String username) {
        setUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    public void setFirstname(final String firstName) {
        setFirstName(firstName);
    }

    /**
     * {@inheritDoc}
     */
    public void setLastname(final String lastName) {
        setLastName(lastName);
    }

    /**
     * {@inheritDoc}
     */
    public void setPrimaryPassword_encrypt(final Object isEncrypted) {
        // Nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(final Object enabled) {
        _enabled = (boolean) enabled;
    }

    public void setVerified(final Object verified) {
        _verified = (boolean) verified;
    }

    @Override
    public String toString() {
        return "class User {\n" +
               "  id: " + _id + "\n" +
               "  username: " + _username + "\n" +
               "  firstName: " + _firstName + "\n" +
               "  lastName: " + _lastName + "\n" +
               "  email: " + _email + "\n" +
               "  dbName: " + _dbName + "\n" +
               "  password: " + _password + "\n" +
               "  salt: " + _salt + "\n" +
               "  lastModified: " + _lastModified + "\n" +
               "  lastSuccessfulLogin: " + _lastSuccessfulLogin + "\n" +
               "  authorization: " + _authorization + "\n" +
               "}\n";
    }

    public User(final int id, final String username, final String first, final String last, final String email, final String dbName, final String password, final String salt, final boolean secured, final Date lastModified, final UserAuthI authorization, final boolean enabled, final boolean verified, final Date lastSuccessfulLogin) {
        _id = id;
        _username = username;
        _firstName = first;
        _lastName = last;
        _email = email;
        _dbName = dbName;
        _password = password;
        _salt = salt;
        _secured = secured;
        _lastModified = lastModified;
        _authorization = authorization;
        _enabled = enabled;
        _verified = verified;
        _lastSuccessfulLogin = lastSuccessfulLogin;
    }

    public User() {

    }

    private Integer   _id;
    private String    _username;
    private String    _firstName;
    private String    _lastName;
    private String    _email;
    private String    _dbName;
    private String    _password;
    private String    _salt;
    private boolean   _secured;
    private Date      _lastModified;
    private UserAuthI _authorization;
    private Boolean   _enabled;
    private Boolean   _verified;
    private Date      _lastSuccessfulLogin;
}
