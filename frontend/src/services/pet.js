import api from './api';

export const uploadImage = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await api.post('/pets/upload-image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });

  const data = response.data;
  if (typeof data === 'string') {
    return data.replace(/^"(.*)"$/, '$1');
  }
  if (data?.secure_url) {
    return data.secure_url;
  }
  if (data?.url) {
    return data.url;
  }

  throw new Error('Unexpected upload response format');
};

export const addPet = async (data) => {
  const response = await api.post('/pets/add', data);
  return response.data;
};

export const getAllPets = async ({ location = '', type = '' } = {}) => {
  const params = {};
  if (location) {
    params.location = location;
  }
  if (type) {
    params.type = type;
  }

  const response = await api.get('/pets', {
    params
  });
  return response.data;
};

export const getPetById = async (id) => {
  const response = await api.get(`/pets/${id}`);
  return response.data;
};

export const getMyPets = async () => {
  const response = await api.get('/pets/my-pets');
  return response.data;
};

export const updatePet = async (id, data) => {
  const response = await api.put(`/pets/${id}`, data);
  return response.data;
};

export const deletePet = async (id) => {
  const response = await api.delete(`/pets/${id}`);
  return response.data;
};
