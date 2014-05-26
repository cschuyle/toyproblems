#!/usr/bin/env ruby

require 'fileutils'
require 'securerandom'

templates = ['alarm', 'door', 'img', 'alarm-2', 'well-formed-door', 'long-alarm']
while true do
  source = templates[SecureRandom.random_number (templates.length)]
  FileUtils.cp "./input/#{source}.json", "./input/tmp-#{source}-#{SecureRandom.uuid}.json"
  sleep SecureRandom.random_number
end
