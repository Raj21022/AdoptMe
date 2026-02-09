import api from './api';

export const getInbox = async () => {
  const response = await api.get('/chat/inbox');
  return response.data;
};
