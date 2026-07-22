terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Sketch: local state. For teams use S3 + DynamoDB locking.
  # backend "s3" {
  #   bucket         = "my-tfstate"
  #   key            = "cloudlab/lab/terraform.tfstate"
  #   region         = "ap-northeast-2"
  #   dynamodb_table = "tf-locks"
  #   encrypt        = true
  # }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "cloudlab"
      Environment = "lab"
      ManagedBy   = "terraform"
      Version     = "v2-sketch"
    }
  }
}
