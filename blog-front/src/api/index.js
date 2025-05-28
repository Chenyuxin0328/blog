import * as postApi from './post'
import * as userApi from './user'
import * as docApi from './doc'

export const post = postApi
export const user = userApi
export const doc = docApi

export default {
  post: postApi,
  user: userApi,
  doc: docApi
} 