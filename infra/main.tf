# main.tf - 메인 Terraform 설정

terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# AWS Provider 설정
provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

# VPC 모듈
module "vpc" {
  source = "./modules/vpc"

  project_name = var.project_name
  environment  = var.environment
  vpc_cidr     = var.vpc_cidr
}

# RDS 모듈
module "rds" {
  source = "./modules/rds"

  project_name      = var.project_name
  environment       = var.environment
  vpc_id            = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
  db_name           = var.db_name
  db_username       = var.db_username
  db_password       = var.db_password
  db_instance_class = var.db_instance_class
  db_security_group_id = module.vpc.db_security_group_id
}

# EC2 인스턴스 모듈
module "ec2" {
  source = "./modules/ec2_instance"

  project_name      = var.project_name
  environment       = var.environment
  instance_type     = var.instance_type
  key_name          = var.key_name
  subnet_id         = module.vpc.public_subnet_ids[0]
  security_group_id = module.vpc.web_security_group_id
}

# ECS 클러스터 모듈
module "ecs_cluster" {
  source = "./modules/ecs_cluster"

  project_name = var.project_name
  environment  = var.environment
}

# ECS 서비스 모듈
module "ecs_service" {
  source = "./modules/ecs_service"

  project_name       = var.project_name
  environment        = var.environment
  cluster_id         = module.ecs_cluster.cluster_id
  subnet_ids         = module.vpc.public_subnet_ids
  security_group_id  = module.vpc.web_security_group_id
}

# Outputs
output "vpc_id" {
  description = "VPC ID"
  value       = module.vpc.vpc_id
}

output "ec2_public_ip" {
  description = "EC2 퍼블릭 IP"
  value       = module.ec2.public_ip
}

output "rds_endpoint" {
  description = "RDS 엔드포인트"
  value       = module.rds.endpoint
}

output "ecs_cluster_name" {
  description = "ECS 클러스터 이름"
  value       = module.ecs_cluster.cluster_name
}
