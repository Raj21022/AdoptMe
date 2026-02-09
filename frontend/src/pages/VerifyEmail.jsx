import React from 'react';
import { Navigate } from 'react-router-dom';

function VerifyEmail() {
  return <Navigate to="/verify-otp" replace />;
}

export default VerifyEmail;
