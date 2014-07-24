# -*- mode: ruby -*-
# vi: set ft=ruby :
# vagrant plugins required:
# vagrant-berkshelf, vagrant-omnibus, vagrant-hostsupdater

Vagrant.configure("2") do |config|
  if !Vagrant.has_plugin?("vagrant-berkshelf")
    raise "Please install vagrant-berkshelf 2.0.1 via `vagrant plugin install vagrant-berkshelf --plugin-version=2.0.1`"
  end

  if !Vagrant.has_plugin?("vagrant-omnibus")
    raise "Please install vagrant-omnibus via `vagrant plugin install vagrant-ominbus --plugin-version=1.4.1`"
  end

  if !Vagrant.has_plugin?("vagrant-hostsupdater")
    raise "Please install vagrant-hostsupdater via `vagrant plugin install vagrant-hostsupdater`"
  end

  config.vm.box = "opscode_ubuntu-12.04_provisionerless"
  config.vm.box_url = "https://opscode-vm-bento.s3.amazonaws.com/vagrant/opscode_ubuntu-12.04_provisionerless.box"

  private_ip = '192.168.33.11'
  mysql_password = "mesos7mysql"

  # enable plugins
  config.berkshelf.enabled = true
  config.omnibus.chef_version = :latest

  if Vagrant.has_plugin?("vagrant-cachier")
    config.cache.auto_detect = true
  end
  
  config.vm.hostname = 'vagrant-singularity'
  config.vm.network :private_network, ip: private_ip

  config.vm.provision :chef_solo do |chef|
    chef.log_level = :debug
    chef.add_recipe "singularity"

    # You may also specify custom JSON attributes:
    chef.json = {
      :mysql => {
        :server_root_password => "#{mysql_password}",
        :server_repl_password => "#{mysql_password}",
        :server_debian_password => "#{mysql_password}",
        :bind_address => "0.0.0.0",
        :allow_remote_root => true
      },
      :mesos => {
        :master => {
          :hostname => config.vm.hostname
        },
        :slave => {
          :hostname => config.vm.hostname
        }
      }
    }
  end
end
