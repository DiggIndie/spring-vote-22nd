# blue/main.tf - Blue EC2 환경

terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "ap-northeast-2"

  default_tags {
    tags = {
      Project     = "spring-vote"
      Environment = "blue"
      ManagedBy   = "Terraform"
    }
  }
}

# 기존 인프라 참조
data "aws_vpc" "main" {
  tags = {
    Name = "spring-vote-dev-vpc"
  }
}

data "aws_subnet" "public" {
  vpc_id = data.aws_vpc.main.id

  filter {
    name   = "tag:Name"
    values = ["spring-vote-dev-public-1"]
  }
}

data "aws_security_group" "web" {
  vpc_id = data.aws_vpc.main.id

  filter {
    name   = "group-name"
    values = ["spring-vote-dev-web-sg"]
  }
}

# Amazon Linux 2023 AMI
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# Blue EC2 인스턴스
resource "aws_instance" "blue" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = "t2.nano"
  key_name               = "terraform"
  subnet_id              = data.aws_subnet.public.id
  vpc_security_group_ids = [data.aws_security_group.web.id]

  associate_public_ip_address = true

  root_block_device {
    volume_type           = "gp3"
    volume_size           = 30
    delete_on_termination = true
    encrypted             = true
  }

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              yum install -y docker
              systemctl start docker
              systemctl enable docker
              usermod -a -G docker ec2-user
              EOF

  tags = {
    Name = "spring-vote-blue"
  }
}

output "blue_instance_id" {
  value = aws_instance.blue.id
}

output "blue_public_ip" {
  value = aws_instance.blue.public_ip
}