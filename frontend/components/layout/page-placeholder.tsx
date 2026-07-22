import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

type PagePlaceholderProps = {
  title: string;
  description: string;
  upcoming?: string[];
};

export function PagePlaceholder({
  title,
  description,
  upcoming = [],
}: PagePlaceholderProps) {
  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <CardTitle className="text-base">{title}</CardTitle>
            <Badge variant="outline">Shell</Badge>
          </div>
          <CardDescription>{description}</CardDescription>
        </CardHeader>
        <CardContent className="space-y-3 text-sm text-muted-foreground">
          <p>
            Step 4에서는 라우트·레이아웃 골격만 제공합니다. 실데이터 연동은 Step 10
            (Dashboard 연결)에서 Platform API 어댑터와 연결합니다.
          </p>
          {upcoming.length > 0 ? (
            <ul className="list-inside list-disc space-y-1">
              {upcoming.map((item) => (
                <li key={item}>{item}</li>
              ))}
            </ul>
          ) : null}
        </CardContent>
      </Card>
    </div>
  );
}
