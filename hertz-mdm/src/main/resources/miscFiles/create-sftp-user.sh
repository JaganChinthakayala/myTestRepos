#!/bin/bash

# fail immediately for any error
set -e

# Must be run as root. sudo preferred
if [[ $EUID -ne 0 ]]; then
  echo "$0 must be run as root. 'sudo' preferred" 1>&2
  exit 1
fi

read_with_default()
{
    local prompt=$1
    shift
    local default=$@
    if [ x"$default" != "x" ]; then
        prompt="$prompt (default \"$default\")"
    fi
    local value=""
    read -p "$prompt " value
    if [ x"$value" == "x" ]; then
        echo "$default"
    else
        echo "$value"
    fi
}

SFTP_DIR=/usr/share/ebx/ebx_interface_files
SFTP_GROUP=sftponly

# Add 'sftponly' group if it doesn't already exist
grep -E "^${SFTP_GROUP}:" /etc/group >/dev/null 2>&1 || groupadd ${SFTP_GROUP}

# Prompt for name of new SFTP user
USER=$(read_with_default "New SFTP user id:" "infact")

# Add sftponly user
useradd $USER -g $SFTP_GROUP -s /bin/bash

# Prompt for name of EBX admin user
EBX_USER=$(read_with_default "EBX admin username:" "ebx")

# Make sure the SFTP directory is writable by the 'sftponly' group
chown -R ${EBX_USER}:${SFTP_GROUP} $SFTP_DIR
chmod -R g+w $SFTP_DIR

# Link transfer directory into new user's home directory
# so it is available by default for the user and
# set ownership for the link
ln -s $SFTP_DIR /home/$USER/
chown -R ${USER}:${SFTP_GROUP} /home/$USER/$(basename $SFTP_DIR)

# Set the password for the new user
passwd $USER
