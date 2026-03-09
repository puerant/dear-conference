import request from '@/utils/request'
import type { PageData } from '@/utils/request'

export interface Dictionary {
  id: number
  dictCode: string
  dictName: string
  description: string
  status: number
  sort: number
  itemCount?: number
}

export interface DictionaryItem {
  id: number
  dictCode: string
  itemCode: string
  itemName: string
  itemValue: number
  description: string
  status: number
  sort: number
}

export interface DictionaryForm {
  dictCode: string
  dictName: string
  description: string
  sort: number
}

export interface DictionaryItemForm {
  dictCode: string
  itemCode: string
  itemName: string
  itemValue: number | string
  description: string
  sort: number | string
}

export const dictionaryApi = {
  getList(params?: { page?: number; pageSize?: number; status?: number }) {
    return request.get<PageData<Dictionary>, PageData<Dictionary>>('/dict', { params })
  },

  getDetail(id: number) {
    return request.get<Dictionary, Dictionary>(`/dict/${id}`)
  },

  create(data: DictionaryForm) {
    return request.post<Dictionary, Dictionary>('/dict', data)
  },

  update(id: number, data: DictionaryForm) {
    return request.put<Dictionary, Dictionary>(`/dict/${id}`, data)
  },

  delete(id: number) {
    return request.delete(`/dict/${id}`)
  },

  getItems(dictCode: string) {
    return request.get<DictionaryItem[], DictionaryItem[]>(`/dict/${dictCode}/items`)
  },

  createItem(data: DictionaryItemForm) {
    return request.post<DictionaryItem, DictionaryItem>('/dict/items', data)
  },

  updateItem(id: number, data: Partial<DictionaryItemForm>) {
    return request.put<DictionaryItem, DictionaryItem>(`/dict/items/${id}`, data)
  },

  deleteItem(id: number) {
    return request.delete(`/dict/items/${id}`)
  }
}
