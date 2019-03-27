#!/bin/sh

gcloud functions deploy PocketPicker --runtime go111 --trigger-http --project hazel-proxy-235401
