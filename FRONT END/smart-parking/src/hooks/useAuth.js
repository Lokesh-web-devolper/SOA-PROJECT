import { useAuth as useAuthHook } from '../context/AuthContext';

export const useAuth = () => {
  return useAuthHook();
};

export default useAuth;
