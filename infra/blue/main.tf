# blue/main.tf - Blue 환경 배포 설정

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

data "aws_subnets" "public" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.main.id]
  }

  filter {
    name   = "tag:Type"
    values = ["Public"]
  }
}

data "aws_security_group" "web" {
  vpc_id = data.aws_vpc.main.id

  filter {
    name   = "group-name"
    values = ["spring-vote-dev-web-sg"]
  }
}

data "aws_ecs_cluster" "main" {
  cluster_name = "spring-vote-dev-cluster"
}

# Blue ECS Service
module "ecs_service_blue" {
  source = "../modules/ecs_service"

  project_name      = "spring-vote"
  environment       = "blue"
  cluster_id        = data.aws_ecs_cluster.main.id
  subnet_ids        = data.aws_subnets.public.ids
  security_group_id = data.aws_security_group.web.id
  container_image   = "your-ecr-repo:blue"  # TODO: 실제 이미지로 변경
  container_port    = 8080
  desired_count     = 1
}

output "blue_service_name" {
  value = module.ecs_service_blue.service_name
}
