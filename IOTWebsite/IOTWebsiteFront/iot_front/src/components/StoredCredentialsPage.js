import React, { useState, useEffect } from "react";

const StoredCredentialsPage = () => {
  const [storedUsers, setStoredUsers] = useState([]);

  useEffect(() => {
    const users = JSON.parse(localStorage.getItem("registeredUsers")) || [];
    setStoredUsers(users);
  }, []);

  return (
    <div>
      <h2>Stored Credentials</h2>
      <table border="1">
        <thead>
          <tr>
            <th>Company ID</th>
            <th>Contact Name</th>
            <th>Password</th>
          </tr>
        </thead>
        <tbody>
          {storedUsers.map((user, index) => (
            <tr key={index}>
              <td>{user.company_id}</td>
              <td>{user.contact_name}</td>
              <td>{user.password}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default StoredCredentialsPage;
